FROM php:8.2-fpm

WORKDIR /var/www/html

# Installation des dépendances système et PHP
RUN apt-get update && apt-get install -y \
    git \
    unzip \
    libpq-dev \
    && docker-php-ext-install pdo pdo_pgsql

# Installation de Composer
RUN curl -sS https://getcomposer.org/installer | php -- --install-dir=/usr/local/bin --filename=composer

# Les dépendances seront installées au démarrage
COPY composer.* ./
RUN composer install --no-scripts --no-autoloader

EXPOSE 8000

CMD sh -c "composer install && php artisan serve --host=0.0.0.0"
