FROM mariadb:11.8

SHELL ["/bin/bash", "-c"]

# Move data out of volume so that it can be snapshoted
RUN echo $'[server] \n\
[mariadbd] \n\
datadir=/var/lib/mysql2' > /etc/mysql/mariadb.conf.d/99-server-custom.cnf
