FROM ubuntu:22.04

RUN apt-get update && apt-get install -y clang-format sudo make

RUN adduser --disabled-password --gecos '' user
RUN echo 'user ALL=(root) NOPASSWD:ALL' > /etc/sudoers.d/user

USER user
WORKDIR /home/user
