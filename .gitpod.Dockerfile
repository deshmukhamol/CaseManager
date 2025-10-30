FROM gitpod/workspace-full:latest

RUN sudo apt-get update \
 && sudo apt-get install -y openjdk-17-jdk maven \
 && sudo rm -rf /var/lib/apt/lists/*
