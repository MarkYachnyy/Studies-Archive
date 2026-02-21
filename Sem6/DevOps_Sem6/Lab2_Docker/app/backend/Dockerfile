#Базовый образ
FROM node:14.16.1
#Сначала будем работать в каталоге frontend
WORKDIR /usr/src/app/backend/
#Сначала копируем только файлы с зависимостями
COPY package.json .
COPY package-lock.json .
#Устанавливаем зависимости
RUN npm install
#Копируем все остальные файлы бэкенда
COPY . .
#Открываем 3001 порт
EXPOSE 3001
#Запускаем бэкенд
CMD [ "npm", "run", "start" ]