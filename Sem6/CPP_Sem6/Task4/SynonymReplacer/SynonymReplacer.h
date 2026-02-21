
// SynonymReplacer.h: главный файл заголовка для приложения PROJECT_NAME
//

#pragma once

#ifndef __AFXWIN_H__
	#error "включить pch.h до включения этого файла в PCH"
#endif

#include "resource.h"		// основные символы


// CSynonymReplacerApp:
// Сведения о реализации этого класса: SynonymReplacer.cpp
//

class CSynonymReplacerApp : public CWinApp
{
public:
	CSynonymReplacerApp();

// Переопределение
public:
	virtual BOOL InitInstance();

// Реализация

	DECLARE_MESSAGE_MAP()
};

extern CSynonymReplacerApp theApp;
