# Build wrapper para Windows.
#
# Por que isso existe: o JVM deriva -Djava.library.path do PATH da máquina. Se o
# PATH tiver aspas ou espaços em um caminho (aqui tinha um "C:\Program Files\
# GitHub CLI" com aspa perdida), o launcher do Java divide o argumento no meio,
# o Gradle Test Executor morre com "ClassNotFoundException: Files\Git\cmd;C:\Program"
# e nenhum teste roda. Nenhum ajuste no build.gradle resolve, porque o valor vem
# do PATH do daemon.
#
# Este script saneia o PATH só para este processo e chama o Gradle normalmente.
# Uso:  .\tools\build.ps1 testDebugUnitTest assembleDebug

param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]] $Tarefas = @("assembleDebug")
)

$env:PATH = (($env:PATH -split ';') | ForEach-Object { $_ -replace '"', '' }) -join ';'

# JAVA_HOME desta máquina aponta para um JDK que não existe mais. Só confia
# nele se o java.exe estiver lá; senão deriva do java que está no PATH.
if (-not $env:JAVA_HOME -or -not (Test-Path (Join-Path $env:JAVA_HOME "bin\java.exe"))) {
    $java = (Get-Command java -ErrorAction SilentlyContinue).Source
    if (-not $java) { Write-Error "java não encontrado no PATH"; exit 1 }
    $env:JAVA_HOME = Split-Path (Split-Path $java -Parent) -Parent
    Write-Host "JAVA_HOME ajustado para $env:JAVA_HOME" -ForegroundColor DarkGray
}

$gradlew = Join-Path (Split-Path $PSScriptRoot -Parent) "gradlew.bat"
& $gradlew @Tarefas --console=plain
exit $LASTEXITCODE
