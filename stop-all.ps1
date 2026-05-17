# Зупиняє процеси, що слухають порти 8080/8081/8082.
foreach ($port in 8080, 8081, 8082) {
    $conns = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
    if ($conns) {
        foreach ($c in $conns) {
            try {
                $proc = Get-Process -Id $c.OwningProcess -ErrorAction Stop
                Write-Host "Killing $($proc.ProcessName) (pid=$($proc.Id)) on port $port"
                Stop-Process -Id $proc.Id -Force
            } catch {
                Write-Host "Could not stop pid $($c.OwningProcess): $_"
            }
        }
    } else {
        Write-Host "Port $port already free"
    }
}
