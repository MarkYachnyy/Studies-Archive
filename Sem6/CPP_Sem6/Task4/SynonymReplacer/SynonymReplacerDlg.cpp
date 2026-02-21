#include "pch.h"
#include "SynonymReplacerDlg.h"
#include "afxdialogex.h"
#include <sstream>
#include <algorithm>

CSynonymAppDlg::CSynonymAppDlg(CWnd* pParent)
    : CDialogEx(IDD_SYNONYMREPLACER_DIALOG, pParent)
{
    m_logger.Log(LogLevel::INFO, L"Application started");
}

void CSynonymAppDlg::DoDataExchange(CDataExchange* pDX)
{
    CDialogEx::DoDataExchange(pDX);
    DDX_Control(pDX, IDC_EDIT_INPUT, m_editInputPath);
    DDX_Control(pDX, IDC_EDIT_SYNONYMS, m_editSynonymsPath);
    DDX_Control(pDX, IDC_EDIT_OUTPUT, m_editOutputPath);
}

BOOL CSynonymAppDlg::OnInitDialog()
{
    CDialogEx::OnInitDialog();
    UpdateLog();
    return TRUE;
}

void CSynonymAppDlg::OnBnClickedButtonLoadInput()
{
    CFileDialog dlg(TRUE, L".txt", nullptr, OFN_FILEMUSTEXIST, L"Text Files (*.txt)|*.txt|All Files (*.*)|*.*||");
    if (dlg.DoModal() == IDOK)
    {
        m_inputFilePath = dlg.GetPathName();
        m_editInputPath.SetWindowText(m_inputFilePath.c_str());
        m_logger.Log(LogLevel::INFO, L"Input file selected: " + m_inputFilePath);
        UpdateLog();
    }
}

void CSynonymAppDlg::OnBnClickedButtonLoadSynonyms()
{
    CFileDialog dlg(TRUE, L".txt", nullptr, OFN_FILEMUSTEXIST, L"Text Files (*.txt)|*.txt|All Files (*.*)|*.*||");
    if (dlg.DoModal() == IDOK)
    {
        m_synonymsFilePath = dlg.GetPathName();
        m_editSynonymsPath.SetWindowText(m_synonymsFilePath.c_str());
        m_logger.Log(LogLevel::INFO, L"Synonyms file selected: " + m_synonymsFilePath);

        if (LoadSynonyms())
        {
            m_logger.Log(LogLevel::INFO, L"Synonyms loaded successfully");
        }
        else
        {
            m_logger.Log(LogLevel::ERR, L"Failed to load synonyms");
        }

        UpdateLog();
    }
}

bool CSynonymAppDlg::LoadSynonyms()
{
    try
    {
        m_synonyms.clear();
        std::wifstream file(m_synonymsFilePath);

        if (!file.is_open())
        {
            throw std::runtime_error("Failed to open synonyms file");
        }

        std::wstring line;
        while (std::getline(file, line))
        {
            std::wstringstream ss(line);
            std::wstring word;
            std::vector<std::wstring> synonyms;

            ss >> word;
            std::wstring synonym;
            while (ss >> synonym)
            {
                synonyms.push_back(synonym);
            }

            if (!word.empty() && !synonyms.empty())
            {
                m_synonyms[word] = synonyms;
            }
        }

        return true;
    }
    catch (const std::exception& e)
    {
        ShowError(L"Error loading synonyms: " + std::wstring(e.what(), e.what() + strlen(e.what())));
        return false;
    }
}

void CSynonymAppDlg::OnBnClickedButtonProcess()
{
    CFileDialog dlg(FALSE, L".txt", L"output.txt", OFN_HIDEREADONLY | OFN_OVERWRITEPROMPT, L"Text Files (*.txt)|*.txt|All Files (*.*)|*.*||");
    if (dlg.DoModal() == IDOK)
    {
        m_outputFilePath = dlg.GetPathName();
        m_editOutputPath.SetWindowText(m_outputFilePath.c_str());
        m_logger.Log(LogLevel::INFO, L"Output file selected: " + m_outputFilePath);

        if (ProcessFile())
        {
            m_logger.Log(LogLevel::INFO, L"File processed successfully");
        }
        else
        {
            m_logger.Log(LogLevel::ERR, L"Failed to process file");
        }

        UpdateLog();
    }
}

bool CSynonymAppDlg::ProcessFile()
{
    try
    {
        if (m_inputFilePath.empty() || m_outputFilePath.empty())
        {
            throw std::runtime_error("Input or output file not selected");
        }

        if (m_synonyms.empty())
        {
            throw std::runtime_error("No synonyms loaded");
        }

        std::wifstream inputFile(m_inputFilePath);
        std::wofstream outputFile(m_outputFilePath);

        if (!inputFile.is_open() || !outputFile.is_open())
        {
            throw std::runtime_error("Failed to open input or output file");
        }

        std::wstring word;
        while (inputFile >> word)
        {
            auto it = m_synonyms.find(word);
            if (it != m_synonyms.end())
            {
                outputFile << GetRandomSynonym(word) << L" ";
            }
            else
            {
                outputFile << word << L" ";
            }
        }

        return true;
    }
    catch (const std::exception& e)
    {
        ShowError(L"Error processing file: " + std::wstring(e.what(), e.what() + strlen(e.what())));
        return false;
    }
}

std::wstring CSynonymAppDlg::GetRandomSynonym(const std::wstring& word)
{
    static std::random_device rd;
    static std::mt19937 gen(rd());

    const auto& synonyms = m_synonyms[word];
    std::uniform_int_distribution<> dis(0, static_cast<int>(synonyms.size()) - 1);

    return synonyms[dis(gen)];
}

void CSynonymAppDlg::UpdateLog()
{
    CString logText;
    for (const auto& entry : m_logger.GetLogs())
    {
        std::wstringstream ss;
        std::tm tm;
        localtime_s(&tm, &entry.time);
        ss << std::put_time(&tm, L"%Y-%m-%d %H:%M:%S") << L" ["
            << m_logger.GetLevelString(entry.level) << L"] "
            << entry.message << L"\r\n";
        logText += ss.str().c_str();
    }

}

void CSynonymAppDlg::ShowError(const std::wstring& message)
{
    MessageBox(message.c_str(), L"Error", MB_ICONERROR);
    m_logger.Log(LogLevel::ERR, message);
    UpdateLog();
}

BEGIN_MESSAGE_MAP(CSynonymAppDlg, CDialogEx)
    ON_BN_CLICKED(IDC_BUTTON_PROCESS, &CSynonymAppDlg::OnBnClickedButtonProcess)
    ON_BN_CLICKED(IDC_BUTTON_LOAD_INPUT, &CSynonymAppDlg::OnBnClickedButtonLoadInput)
    ON_BN_CLICKED(IDC_BUTTON_LOAD_SYNONYMS, &CSynonymAppDlg::OnBnClickedButtonLoadSynonyms)
    ON_EN_CHANGE(IDC_EDIT_OUTPUT, &CSynonymAppDlg::OnEnChangeEditOutput)
END_MESSAGE_MAP()

void CSynonymAppDlg::OnBnClickedButtonLoadOutput()
{
    // TODO: добавьте свой код обработчика уведомлений
}

void CSynonymAppDlg::OnEnChangeEditOutput()
{
    // TODO:  Если это элемент управления RICHEDIT, то элемент управления не будет
    // send this notification unless you override the CDialogEx::OnInitDialog()
    // функция и вызов CRichEditCtrl().SetEventMask()
    // with the ENM_CHANGE flag ORed into the mask.

    // TODO:  Добавьте код элемента управления
}
