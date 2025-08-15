# Redis Setup для Intershop

## Запуск Redis

### Вариант 1: Docker Compose (рекомендуется)
```bash
# Запуск Redis
docker-compose up -d

# Проверка статуса
docker-compose ps

# Остановка
docker-compose down
```

### Вариант 2: Docker напрямую
```bash
docker run -d --name intershop-redis -p 6379:6379 redis:7-alpine
```

### Вариант 3: Локальная установка
```bash
# Ubuntu/Debian
sudo apt-get install redis-server

# macOS
brew install redis

# Запуск
redis-server
```

## Проверка подключения

```bash
# Подключение к Redis CLI
redis-cli

# Проверка ping
127.0.0.1:6379> ping
PONG

# Проверка ключей
127.0.0.1:6379> keys *
```

## Конфигурация

Redis настроен с:
- **Порт**: 6379
- **Максимальная память**: 256MB
- **Политика памяти**: LRU (Least Recently Used)
- **Персистентность**: AOF (Append Only File)

## Кеширование в приложении

### Ключи кеша:
- `item:{id}` - отдельный товар
- `items:{id1,id2,id3}` - список товаров
- `search:{query}:{page}:{size}:{sort}` - результаты поиска

### TTL (Time To Live):
- **Товары**: 2 часа
- **Поиск**: 2 часа
- **Списки**: 2 часа

## Мониторинг

```bash
# Статистика Redis
redis-cli info

# Мониторинг команд в реальном времени
redis-cli monitor

# Проверка использования памяти
redis-cli info memory
```

## Troubleshooting

### Redis не запускается:
```bash
# Проверка логов
docker logs intershop-redis

# Проверка портов
netstat -tlnp | grep 6379
```

### Проблемы подключения:
1. Проверьте, что Redis запущен
2. Проверьте порт 6379
3. Проверьте firewall/антивирус
4. Проверьте конфигурацию в `application.yaml`
