#include "pch.h"
#include "Logger.h"
#include <sstream>
#include <iomanip>

Logger::Logger()
{
}

void Logger::Log(LogLevel level, const std::wstring& message)
{
    LogEntry entry;
    entry.time = std::time(nullptr);
    entry.level = level;
    entry.message = message;
    m_logs.push_back(entry);
    std::wofstream file(L"app.log", std::ios::app);
    
    std::tm tm;
    localtime_s(&tm, &entry.time);
    file << std::put_time(&tm, L"%Y-%m-%d %H:%M:%S") << L" ["
                << GetLevelString(entry.level) << L"] "
                << entry.message << std::endl;
      
}

const std::vector<LogEntry>& Logger::GetLogs() const
{
    return m_logs;
}

std::wstring Logger::GetLevelString(LogLevel level) const
{
    switch (level)
    {
        case LogLevel::INFO: return L"INFO";
        case LogLevel::WARNING: return L"WARNING";
        case LogLevel::ERR: return L"ERROR";
        default: return L"UNKNOWN";
    }
}