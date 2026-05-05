$ErrorActionPreference = "Stop"

Write-Host "===============================================" -ForegroundColor Cyan
Write-Host " VibeTube V5 Estavel - Envio para GitHub" -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Este script vai enviar o projeto para:" -ForegroundColor Yellow
Write-Host "https://github.com/jardeltecnologia-sys/vibetube-android-bridge" -ForegroundColor Yellow
Write-Host ""

function Add-GitToPathIfNeeded {
  if (Get-Command git -ErrorAction SilentlyContinue) { return }

  $possiblePaths = @(
    "C:\Program Files\Git\cmd",
    "C:\Program Files\Git\bin",
    "C:\Program Files (x86)\Git\cmd",
    "C:\Program Files (x86)\Git\bin"
  )

  foreach ($path in $possiblePaths) {
    $gitExe = Join-Path $path "git.exe"
    if (Test-Path $gitExe) {
      $env:Path = "$path;" + $env:Path
      return
    }
  }
}

Add-GitToPathIfNeeded

if (!(Get-Command git -ErrorAction SilentlyContinue)) {
  Write-Host "Git nao encontrado." -ForegroundColor Red
  Write-Host "Instale pelo link oficial: https://git-scm.com/download/win" -ForegroundColor Yellow
  Write-Host "Depois feche e abra o PowerShell novamente." -ForegroundColor Yellow
  exit 1
}

Write-Host "Git encontrado:" -ForegroundColor Green
git --version

if (!(Test-Path "mobile\android\app\build.gradle.kts")) {
  Write-Host "Erro: rode este script dentro da pasta 'vibetube'." -ForegroundColor Red
  Write-Host "A pasta atual precisa conter: .github, mobile, ENVIAR_PARA_GITHUB.ps1" -ForegroundColor Yellow
  exit 1
}

git config --global user.name "JARDEL CASSIMIRO"
git config --global user.email "jardeltecnologia-sys@users.noreply.github.com"

if (!(Test-Path ".git")) {
  git init
}

git branch -M main

$remote = "https://github.com/jardeltecnologia-sys/vibetube-android-bridge.git"
$existingRemote = git remote 2>$null
if ($existingRemote -contains "origin") {
  git remote set-url origin $remote
} else {
  git remote add origin $remote
}

git add .
$hasChanges = git status --porcelain
if ($hasChanges) {
  git commit -m "fix: VibeTube V5 estavel sem audio misturado"
} else {
  Write-Host "Nada novo para commit. Reenviando estado atual." -ForegroundColor Yellow
}

git push --force -u origin main

Write-Host ""
Write-Host "Envio concluido!" -ForegroundColor Green
Write-Host "Agora abra: https://github.com/jardeltecnologia-sys/vibetube-android-bridge/actions" -ForegroundColor Green
