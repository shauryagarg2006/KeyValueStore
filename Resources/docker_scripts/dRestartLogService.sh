echo "Stopping running Log Servers"
dStopLogService.sh
echo "Cleaning Log Files"
dCleanLogs.sh
sleep 1
echo "Starting Log Servers"
dStartLogService.sh