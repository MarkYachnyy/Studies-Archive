
#pragma once
#include "Logger.h"
#include <unordered_map>
#include <vector>
#include <random>
#include <fstream>
#include "resource.h"

class CSynonymAppDlg : public CDialogEx
{
public:
    CSynonymAppDlg(CWnd* pParent = nullptr);

    enum { IDD = IDD_SYNONYMREPLACER_DIALOG };

protected:
    virtual void DoDataExchange(CDataExchange* pDX);
    virtual BOOL OnInitDialog();

    // Обработчики кнопок
    afx_msg void OnBnClickedButtonProcess();
    afx_msg void OnBnClickedButtonLoadInput();
    afx_msg void OnBnClickedButtonLoadSynonyms();
    afx_msg void OnBnClickedButtonLoadOutput();

    DECLARE_MESSAGE_MAP()

private:
    // Логгер
    Logger m_logger;

    // Структура данных для синонимов
    std::unordered_map<std::wstring, std::vector<std::wstring>> m_synonyms;

    // Пути к файлам
    std::wstring m_inputFilePath;
    std::wstring m_synonymsFilePath;
    std::wstring m_outputFilePath;

    // Элементы управления
    CEdit m_editInputPath;
    CEdit m_editSynonymsPath;
    CEdit m_editOutputPath;

    CButton m_buttonLoadInput;
    CButton m_buttonLoadSynonyms;
    CButton m_buttonLoadOutput;
    CButton m_ButtonProcess;

    // Методы
    bool LoadSynonyms();
    bool ProcessFile();
    std::wstring GetRandomSynonym(const std::wstring& word);
    void UpdateLog();
    void ShowError(const std::wstring& message);
public:
    afx_msg void OnEnChangeEditOutput();
};