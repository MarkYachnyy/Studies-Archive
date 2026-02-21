#pragma once
#include <string>
#include <vector>
#include <fstream>
#include <ctime>
#include <iomanip>

enum class LogLevel { INFO, WARNING, ERR };

struct LogEntry
{
    std::time_t time;
    LogLevel level;
    std::wstring message;
};

class Logger
{
public:
    Logger();
    void Log(LogLevel level, const std::wstring& message);
    const std::vector<LogEntry>& GetLogs() const;
    std::wstring GetLevelString(LogLevel level) const;

private:
    std::vector<LogEntry> m_logs;
};