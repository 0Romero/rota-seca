# Gera docs/documentacao.pdf a partir de docs/documentacao.html usando o Microsoft Edge (headless).
# Uso, na pasta ddd-java:  powershell -ExecutionPolicy Bypass -File docs/gerar-pdf.ps1
$edge = "C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe"
$html = (Resolve-Path "$PSScriptRoot\documentacao.html").Path
$pdf  = Join-Path $PSScriptRoot "documentacao.pdf"
$url  = "file:///" + ($html -replace '\\', '/')

& $edge --headless=new --disable-gpu --no-pdf-header-footer --virtual-time-budget=20000 `
        "--print-to-pdf=$pdf" $url | Out-Null
Write-Host "PDF gerado em $pdf"
