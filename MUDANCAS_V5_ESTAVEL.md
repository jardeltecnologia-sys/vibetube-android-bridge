# VibeTube V5 Estável — sem áudio misturado

Esta versão é baseada na última base que gerava build verde no GitHub Actions.

## Estratégia

As versões premium anteriores adicionaram player/controle de áudio mais agressivo e começaram a falhar na compilação.
Esta V5 elimina a causa provável sem mexer em muitos arquivos ao mesmo tempo.

## Mudanças

- App continua estilo feed vertical.
- Card/letreiro superior foi removido do feed.
- YouTube não abre WebView/player embutido dentro do feed.
- YouTube fica como thumbnail no feed, evitando erro 153 e áudio fantasma.
- Vídeos diretos MP4/WebM/HLS continuam usando player nativo.
- Só o item atual do feed cria player, como na base estável.
- Workflow GitHub Actions incluído.
- Script de envio com tentativa de localizar Git no Windows.

## Objetivo

Recuperar build verde e eliminar:
- áudio de vídeo anterior;
- áudio de vídeo seguinte;
- player engasgando por múltiplas WebViews;
- erro 153 do YouTube embutido;
- letreiro/card superior intrusivo.

## Próximo passo depois de aprovado

Quando esta V5 ficar verde e estiver estável no celular, a próxima melhoria deve ser feita em um único arquivo por vez.
