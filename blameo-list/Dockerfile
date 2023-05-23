# 1. For build React app
FROM node:16.20.0-alpine as builder

# RUN apk add --no-cache yarn
# Set working directory
WORKDIR /app
#
COPY *.json ./
RUN yarn install
# Same as yarn install

COPY . /app
RUN yarn build
# 2. For Nginx setup
FROM nginx:1.24.0-alpine
EXPOSE 3000
COPY ./nginx/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html

ENTRYPOINT ["nginx", "-g", "daemon off;"]