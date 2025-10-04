# тут про права пользователя не забываем.
docker run --name my-postgres \
           -e POSTGRES_PASSWORD=mypassword \
           -p 5432:5432 \
           -d postgres:12
# проверить:
#docker ps

#МОЖЕМ ПРОВЕРИТЬ подключение к bd через терминал:

#качаем клиент если его ещё нет
sudo apt install postgresql-client

#подключаемся к базе
psql -h localhost -U postgres -d postgres

#попросит пароль от BD
#mypassword

#Если видите приглашение postgres=# — значит, PostgreSQL работает!
#Выйти из
psql: \q
