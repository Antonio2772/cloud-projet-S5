FROM node:latest

WORKDIR /app

# Copie des fichiers de configuration npm
COPY package*.json ./

# Installation des dépendances
RUN npm install

# Expose le port par défaut de Vite
EXPOSE 80

# Utiliser npm run dev au lieu de serve
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]