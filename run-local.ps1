$ErrorActionPreference = 'Stop'
$appJar = Join-Path $PSScriptRoot 'target/richard-portfolio.jar'
if (-not (Test-Path -LiteralPath $appJar)) {
    throw 'Build the application first: mvn clean package'
}
$portfolioJava = 'java'
if ($env:JAVA_HOME -and (Test-Path -LiteralPath (Join-Path $env:JAVA_HOME 'bin/java.exe'))) {
    $portfolioJava = Join-Path $env:JAVA_HOME 'bin/java.exe'
} else {
    # Portable runtime used for verification in this workspace, if available.
    $portableRoot = Join-Path $PSScriptRoot '../../tmp/portfolio-tools/jdk17'
    if (Test-Path -LiteralPath $portableRoot) {
        $portableJdk = Get-ChildItem -LiteralPath $portableRoot -Directory | Select-Object -First 1
        if ($portableJdk) { $portfolioJava = Join-Path $portableJdk.FullName 'bin/java.exe' }
    }
}
Write-Host 'Starting Richard portfolio at http://localhost:8080 (Java 17 or later required).'
& $portfolioJava -jar $appJar
exit $LASTEXITCODE
