# VibeTube — APK Bridge (Blogger → Cloud)

App Android nativo que consome o feed público do blog **vibetube.com.br** e apresenta os vídeos numa experiência de feed vertical estilo TikTok/Reels/Shorts. Esta é a fase **bridge**: o blog é a fonte oficial enquanto a infraestrutura Google Cloud não estiver pronta.

- **Package:** `br.com.vibetube.app`
- **Modo atual:** `blogger_bridge`
- **Min SDK:** 24 (Android 7.0)  •  **Target SDK:** 34
- **Stack:** Kotlin + Jetpack Compose + Material 3 + Room + Media3/ExoPlayer + OkHttp
- **Link de instalação/teste:** https://appdistribution.firebase.dev/i/538c49560ae4a639

---

## Como funciona

1. App abre → splash → `FeedScreen`
2. `BloggerFeedDataSource` busca `https://www.vibetube.com.br/feeds/posts/default?alt=json&max-results=50`
3. `BloggerHtmlParser` extrai vídeo de cada post (YouTube embed, mp4, webm, m3u8) + thumbnail + intro
4. `VideoMapper` converte para o modelo unificado `VibeVideo`
5. Tudo é salvo em Room (`vibetube.db`)
6. UI consome via `Flow` — quando rede atualiza, UI re-renderiza
7. `BlogIntroDataSource` faz fetch do HTML da home do blog e extrai a introdução pública (texto "Experiência App", "Feed vertical em tela cheia"...)
8. Comentários: leitura via Blogger API v3; escrita via WebView controlada apontando para o formulário oficial do post

## Funcionalidades ativas

| Feature | Status | Implementação |
|---|---|---|
| Feed vertical | ✅ | `FeedScreen` + `VerticalPager` |
| Player YouTube embed | ✅ | `YouTubeEmbedPlayer` (WebView restrita a `youtube-nocookie.com` + alguns hosts) |
| Player mp4/webm/m3u8 | ✅ | `DirectExoPlayer` (Media3) |
| Introdução dinâmica do blog | ✅ | `BlogIntroCard` no topo do feed |
| Cache offline | ✅ | Room com 5 tabelas |
| Compartilhar vídeo | ✅ | Android Sharesheet com texto formatado |
| Convidar para o APK | ✅ | Compartilha link Firebase App Distribution |
| Curtir local | ✅ | Persistido no Room, contador local |
| Salvar local | ✅ | `saved_videos` no Room |
| Comentários (leitura) | ✅ | Blogger API v3 |
| Comentários (escrita) | ✅ | WebView controlada com whitelist |
| Explorar (grid) | ✅ | `ExploreScreen` |

## Em stand-by (acendem em "Em breve")

Login, cadastro, upload, seguir, notificações, chat, ao vivo, monetização, painel do criador, moderação, denúncias, sincronização de curtidas em nuvem.

## Permissões

Apenas duas:
- `INTERNET`
- `ACCESS_NETWORK_STATE`

Nada de câmera, microfone, localização, contatos, mídia ou armazenamento.

---

## Como gerar o APK (GitHub Actions — recomendado)

Não precisa instalar Android Studio. O workflow `.github/workflows/android-build.yml` faz tudo.

1. **Crie um repositório no GitHub** (público ou privado, tanto faz)
2. **Suba todo o conteúdo desta pasta:**
   ```bash
   cd /caminho/onde/voce/extraiu
   git init
   git add .
   git commit -m "feat(android): VibeTube APK blogger social bridge"
   git branch -M main
   git remote add origin https://github.com/SEU_USUARIO/SEU_REPO.git
   git push -u origin main
   ```
3. **Vá em GitHub → seu repo → aba `Actions`**
   - O workflow "Build VibeTube APK" vai rodar automaticamente
   - Demora ~5–8 min na primeira vez (Gradle baixa dependências)
4. **Quando terminar, clique no run mais recente:**
   - Role para baixo → seção **Artifacts**
   - Baixe o ZIP `vibetube-debug-apk`
   - Dentro: `app-debug.apk`
5. **Instale no celular:**
   - Transfira o APK pro Android (USB, Drive, WhatsApp pra você mesmo, etc.)
   - Abra o arquivo no celular
   - Aceite "instalar de fontes desconhecidas" se solicitar
   - Pronto

### Se algo der errado no Actions

- **Falha em "Generate Gradle wrapper"** → significa que o `gradle-wrapper.jar` não está commitado. Solução: na sua máquina, rode `cd mobile/android && gradle wrapper --gradle-version 8.4`, commit e push.
- **Falha em "Run unit tests"** → o workflow falha em vermelho mas o relatório fica em Artifacts → `test-reports`
- **Build leva muito tempo** → a partir do 2º run, o cache do Gradle reduz pra ~3 min

## Como gerar localmente (opcional — se tiver Android Studio)

```bash
cd mobile/android
./gradlew assembleDebug
# APK em: app/build/outputs/apk/debug/app-debug.apk

# Outras opções
./gradlew assembleRelease   # release não-assinado
./gradlew bundleRelease     # AAB para Play Store
./gradlew testDebugUnitTest # roda só os testes
```

## Como mudar feature flags

Edite `mobile/android/app/src/main/assets/vibetube_config.json`. Exemplo: ativar comentários via API direta (quando você implementar isso no futuro):

```json
{
  "features": {
    "commentsWriteViaApi": true,
    "commentsWriteViaBloggerWebView": false
  }
}
```

Depois rebuilde o APK.

## Como migrar para Google Cloud no futuro

1. Implemente sua API REST em algum endpoint (ex.: `https://api.vibetube.com.br`)
2. No `vibetube_config.json`:
   - `mode`: `"cloud"`
   - `cloudApiBaseUrl`: `"https://api.vibetube.com.br"`
   - Ative as features que sua API suporta (`upload`, `login`, `follow`, etc.)
3. Implemente as classes que hoje são stubs:
   - `CloudVideoRepository` — endpoints `GET /api/videos`, `GET /api/videos/{id}`
   - `LikesCloudRepository` — `POST /api/videos/{id}/like`
   - `CloudApiService` — interface já está em `data/repository/CloudApiService.kt`
4. No `VibeTubeApp.videoRepository`, troque a seleção:
   ```kotlin
   val videoRepository by lazy {
       if (featureFlags.isCloudMode())
           CloudVideoRepository(/* ... */)
       else
           BloggerVideoRepository(/* ... */)
   }
   ```
5. Rebuilde

## Segurança da WebView

A única superfície sensível do app são os WebViews. As proteções aplicadas:

- **Whitelist rigorosa** em `SafeWebViewConfig.kt`:
  - WebView de comentários: só `vibetube.com.br`, `*.blogger.com`, `accounts.google.com`, `gstatic.com`, `googleapis.com`
  - WebView de player: só `youtube.com`, `youtube-nocookie.com`, `youtu.be`, `ytimg.com`, `googlevideo.com`
- **HTTPS obrigatório** — qualquer `http://` é bloqueado antes de carregar
- **`allowFileAccess = false`** — WebView não consegue ler arquivos locais
- **`mixedContentMode = NEVER_ALLOW`** — bloqueia HTTP em página HTTPS
- **`shouldInterceptRequest`** retorna body vazio para subrecursos fora da whitelist
- **Bridge JS minimalista** — `SecureBloggerCommentBridge` tem 2 métodos `void`, sem retorno sensível, e não é registrada na tela de comentários (reduzimos a superfície)
- **Reload key força recriação** completa da WebView quando usuário toca atualizar
- **Botão "Abrir no navegador"** é a única forma de sair da whitelist — sai por intent externo

## Estrutura

```
mobile/android/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── assets/vibetube_config.json
│       │   ├── java/br/com/vibetube/app/
│       │   │   ├── core/        — config, network, player, share, webview, utils
│       │   │   ├── data/        — Blogger DTOs, parsers, Room, mappers, repos
│       │   │   ├── domain/      — models, interfaces, use cases
│       │   │   ├── features/    — feed, comments, explore, profile, etc.
│       │   │   └── ui/          — theme, navigation, components
│       │   └── res/             — strings, colors, themes, drawables, mipmap, xml
│       └── test/                — testes JUnit (fakes, sem Mockito)
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradle/wrapper/gradle-wrapper.properties
├── gradlew, gradlew.bat
└── README_APK_BRIDGE.md
```

## Limitações conhecidas

1. **Comentários via API v3** podem retornar 403 sem chave de API ou se o blog tiver permissões restritas. Nesse caso, o app mostra lista vazia e o usuário tem que comentar pelo botão "Comentar no blog" (WebView).
2. **Curtidas são locais** — cada device tem seu próprio estado. Sincronização real virá com a Cloud.
3. **Detector de reação** (`BlogReactionDetector`) só *detecta* o widget; não posta. Postar reação precisaria de fluxo OAuth com a conta do usuário, que está fora do escopo desta fase.
4. **Pré-carregamento** — o player só toca o item da página atual no `VerticalPager`. Os adjacentes não são pré-carregados (economia de banda).
5. **`gradle-wrapper.jar`** não está incluído no ZIP. O workflow do GitHub Actions baixa o Gradle e gera o wrapper na primeira execução. Se quiser pré-gerar localmente, rode `gradle wrapper --gradle-version 8.4` na pasta `mobile/android/`.

## Próximos passos sugeridos

1. **Subir no GitHub e gerar o primeiro APK** pelo Actions
2. **Instalar no seu celular e testar** o feed
3. **Validar a extração** dos vídeos do seu blog (se algum post não aparecer com vídeo, abrir o post e checar se tem `<iframe>` do YouTube ou tag `<video>` direta)
4. **Compartilhar o link Firebase** com beta testers
5. **Quando estiver pronto pra Cloud:** seguir o roteiro "Como migrar para Google Cloud no futuro" acima

---

**Backend Laravel preservado** — este projeto Android é independente. A pasta `mobile/android/` não toca em nada do backend existente. Se você for fazer push num repositório que já tem o Laravel, o `.gitignore` na raiz garante que builds Android, keystores e configs locais não vazem.
