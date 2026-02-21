Ссылка на видео - https://drive.google.com/file/d/1GYUJAqh9eWXwexwmAd0JItzH1thRxOkC/view?usp=drive_link

1. Генерация RSA-ключа: openssl genrsa

openssl genrsa -out private.key -aes256 4096

-out <файл>
Указывает файл для сохранения приватного ключа (например, private.key).

-aes128, -aes192, -aes256
Шифрует ключ с помощью AES (128, 192 или 256 бит). Запрашивает пароль.

-des3
Шифрует ключ алгоритмом 3DES.

<размер> (например, 2048)
Длина ключа в битах (по умолчанию — 2048).

2. Запрос на подпись CSR: openssl req

openssl req -new -key private.key -out request.csr -subj "/C=RU/CN=example.com"

-new
Генерирует новый CSR (запрос на сертификат).

-key <файл>
Указывает приватный ключ для подписи CSR.

-out <файл>
Сохраняет CSR в указанный файл (например, request.csr).

-subj "/C=RU/..."
Задает данные субъекта в командной строке (избегает интерактивного ввода).

3. Самоподписанный сертификат: openssl x509

openssl x509 -req -in request.csr -signkey private.key -out certificate.crt -days 3650

-req
Указывает, что входной файл — это CSR.

-in <файл>
Входной файл (например, request.csr).

-signkey <файл>
Приватный ключ для самоподписи сертификата.

-out <файл>
Сохраняет сертификат в файл (например, certificate.crt).

-days <число>
Срок действия сертификата.

4. Подпись через CA: 

openssl x509 -req -in request.csr \
  -CA ca.crt -CAkey ca_private.key \
  -out certificate.crt -days 365

-CA ca.crt — сертификат CA (публичная часть).

-CAkey ca_private.key — приватный ключ CA (для подписи).

Результат: сертификат, подписанный CA, который будет доверенным, если клиенты имеют ca.crt в списке доверенных корневых сертификатов.

5. Просмотр содержимого сертификата или ключа

openssl x509 -in <файл> -text -noout
Показывает детали сертификата (поля, срок действия и т.д.).

openssl rsa -in <файл> -text -noout
Просмотр информации о приватном RSA-ключе.

openssl req -in <файл> -text -noout
Просмотр содержимого CSR.
