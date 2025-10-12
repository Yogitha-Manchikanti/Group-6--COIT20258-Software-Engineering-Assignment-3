# ============================================
# THS-Enhanced Database Backup Automation
# Member 3 (Database Lead) - Assignment 3
# COIT20258 Group 6
# Date: October 13, 2025
# ============================================

# Configuration
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$date = Get-Date -Format "yyyyMMdd"
$backupDir = "C:\THS_Backups"
$logDir = "C:\THS_Backups\Logs"
$backupFile = "$backupDir\ths_enhanced_backup_$timestamp.sql"
$logFile = "$logDir\backup_log_$date.txt"

# MySQL Configuration
$mysqlPath = "C:\Program Files\MySQL\MySQL Server 9.4\bin\mysqldump.exe"
$mysqlUser = "root"
$database = "ths_enhanced"

# Backup retention (days)
$retentionDays = 30

# ============================================
# Functions
# ============================================

function Write-Log {
    param([string]$Message)
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    $logMessage = "[$timestamp] $Message"
    Write-Host $logMessage
    Add-Content -Path $logFile -Value $logMessage
}

function Test-MySQL {
    if (-not (Test-Path $mysqlPath)) {
        Write-Log "ERROR: MySQL not found at: $mysqlPath"
        Write-Log "Please update the \$mysqlPath variable with correct location"
        return $false
    }
    return $true
}

function Create-Directories {
    if (-not (Test-Path $backupDir)) {
        New-Item -ItemType Directory -Path $backupDir | Out-Null
        Write-Log "Created backup directory: $backupDir"
    }
    
    if (-not (Test-Path $logDir)) {
        New-Item -ItemType Directory -Path $logDir | Out-Null
        Write-Log "Created log directory: $logDir"
    }
}

function Backup-Database {
    param([string]$OutputFile)
    
    Write-Log "Starting backup of database: $database"
    Write-Log "Backup file: $OutputFile"
    
    $startTime = Get-Date
    
    try {
        # Perform backup (user will be prompted for password)
        & $mysqlPath -u $mysqlUser -p --databases $database --routines --triggers --events > $OutputFile
        
        if ($LASTEXITCODE -eq 0) {
            $endTime = Get-Date
            $duration = ($endTime - $startTime).TotalSeconds
            $fileSize = (Get-Item $OutputFile).Length / 1MB
            
            Write-Log "SUCCESS: Backup completed in $([math]::Round($duration, 2)) seconds"
            Write-Log "Backup size: $([math]::Round($fileSize, 2)) MB"
            Write-Log "File location: $OutputFile"
            
            return $true
        } else {
            Write-Log "ERROR: Backup failed with exit code: $LASTEXITCODE"
            return $false
        }
    }
    catch {
        Write-Log "ERROR: Backup failed with exception: $($_.Exception.Message)"
        return $false
    }
}

function Cleanup-OldBackups {
    Write-Log "Cleaning up backups older than $retentionDays days..."
    
    $deletedCount = 0
    $oldBackups = Get-ChildItem $backupDir -Filter "ths_enhanced_backup_*.sql" | 
        Where-Object { $_.LastWriteTime -lt (Get-Date).AddDays(-$retentionDays) }
    
    foreach ($file in $oldBackups) {
        try {
            Remove-Item $file.FullName -Force
            Write-Log "Deleted old backup: $($file.Name)"
            $deletedCount++
        }
        catch {
            Write-Log "ERROR: Failed to delete: $($file.Name) - $($_.Exception.Message)"
        }
    }
    
    Write-Log "Cleanup complete: $deletedCount old backup(s) deleted"
}

function Get-BackupStatistics {
    Write-Log "Backup Statistics:"
    
    $allBackups = Get-ChildItem $backupDir -Filter "ths_enhanced_backup_*.sql"
    $totalSize = ($allBackups | Measure-Object -Property Length -Sum).Sum / 1GB
    $backupCount = $allBackups.Count
    
    Write-Log "Total backups: $backupCount"
    Write-Log "Total size: $([math]::Round($totalSize, 2)) GB"
    
    if ($backupCount -gt 0) {
        $oldest = ($allBackups | Sort-Object LastWriteTime | Select-Object -First 1).LastWriteTime
        $newest = ($allBackups | Sort-Object LastWriteTime -Descending | Select-Object -First 1).LastWriteTime
        
        Write-Log "Oldest backup: $($oldest.ToString('yyyy-MM-dd HH:mm:ss'))"
        Write-Log "Newest backup: $($newest.ToString('yyyy-MM-dd HH:mm:ss'))"
    }
}

function Send-EmailNotification {
    param(
        [string]$Status,
        [string]$Details
    )
    
    # Email configuration (configure if needed)
    $emailEnabled = $false  # Set to $true to enable email notifications
    
    if (-not $emailEnabled) {
        return
    }
    
    # Email settings (update with your SMTP details)
    $smtpServer = "smtp.example.com"
    $smtpPort = 587
    $emailFrom = "ths-backups@example.com"
    $emailTo = "admin@example.com"
    $subject = "THS Database Backup - $Status"
    
    try {
        Send-MailMessage -SmtpServer $smtpServer -Port $smtpPort `
            -From $emailFrom -To $emailTo -Subject $subject `
            -Body $Details -UseSsl
        
        Write-Log "Email notification sent: $Status"
    }
    catch {
        Write-Log "ERROR: Failed to send email: $($_.Exception.Message)"
    }
}

# ============================================
# Main Backup Process
# ============================================

Write-Log "==========================================="
Write-Log "THS-Enhanced Database Backup Script"
Write-Log "==========================================="

# Step 1: Verify MySQL installation
if (-not (Test-MySQL)) {
    Write-Log "Backup aborted: MySQL not found"
    exit 1
}

# Step 2: Create directories
Create-Directories

# Step 3: Perform backup
$backupSuccess = Backup-Database -OutputFile $backupFile

# Step 4: Cleanup old backups
if ($backupSuccess) {
    Cleanup-OldBackups
}

# Step 5: Show statistics
Get-BackupStatistics

# Step 6: Send notification (if enabled)
if ($backupSuccess) {
    $details = "Database backup completed successfully`nFile: $backupFile`nTimestamp: $(Get-Date)"
    Send-EmailNotification -Status "SUCCESS" -Details $details
} else {
    $details = "Database backup FAILED`nCheck log file: $logFile`nTimestamp: $(Get-Date)"
    Send-EmailNotification -Status "FAILED" -Details $details
}

# ============================================
# Summary
# ============================================

Write-Log "==========================================="
if ($backupSuccess) {
    Write-Log "Backup process completed successfully!"
    Write-Log "Backup file: $backupFile"
} else {
    Write-Log "Backup process FAILED!"
    Write-Log "Check the log file for details: $logFile"
}
Write-Log "==========================================="

# Exit with appropriate code
exit $(if ($backupSuccess) { 0 } else { 1 })
