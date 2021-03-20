# mongodb docker container 구동 스크립트
name_codingtest_mongodb='codingtest-mongodb'
cnt_codingtest_mongodb=`docker container ls --filter name=codingtest-mongodb | wc -l`
cnt_codingtest_mongodb=$(($cnt_codingtest_mongodb -1))

if [ $cnt_codingtest_mongodb -eq 0 ]
then
    echo "'$name_codingtest_mongodb' 컨테이너를 구동시킵니다.\n"

    # 디렉터리 존재 여부 체크 후 없으면 새로 생성
    DIRECTORY=~$USER/env/docker/codingtest/volumes/codingtest-mongodb
    test -f $DIRECTORY && echo "볼륨 디렉터리가 존재하지 않으므로 새로 생성합니다.\n"

    if [ $? -lt 1 ]; then
      mkdir -p ~$USER/env/docker/codingtest/volumes/codingtest-mongodb
    fi

    # mongodb 컨테이너 구동 & 볼륨 마운트
    docker container run --rm -d -p 27039:27017 --name codingtest-mongodb \
                -v ~/env/docker/codingtest/volumes/codingtest-mongodb:/data/db \
                -e MONGO_INITDB_DATABASE=codingtest \
                -e MONGO_INITDB_ROOT_USERNAME=mongoadmin \
                -e MONGO_INITDB_ROOT_PASSWORD=1111 \
                -d mongo:3.6.22-xenial

else
    echo "'$name_codingtest_mongodb' 컨테이너가 존재합니다. 기존 컨테이너를 중지하고 삭제합니다."
    # 컨테이너 중지 & 삭제
    docker container stop codingtest-mongodb

    # 컨테이너 볼륨 삭제
    rm -rf ~/env/docker/codingtest/volumes/codingtest-mongodb/*
    echo "\n'$name_codingtest_mongodb' 컨테이너 삭제를 완료했습니다.\n"

    # 디렉터리 존재 여부 체크 후 없으면 새로 생성
    DIRECTORY=~$USER/env/docker/codingtest/volumes/codingtest-mongodb
    test -f $DIRECTORY && echo "볼륨 디렉터리가 존재하지 않으므로 새로 생성합니다.\n"

    if [ $? -lt 1 ]; then
      mkdir -p ~$USER/env/docker/codingtest/volumes/codingtest-mongodb
    fi

    # mongodb 컨테이너 구동 & 볼륨 마운트
    docker container run --rm -d -p 27039:27017 --name codingtest-mongodb \
                -v ~/env/docker/codingtest/volumes/codingtest-mongodb:/data/db \
                -e MONGO_INITDB_DATABASE=codingtest \
                -e MONGO_INITDB_ROOT_USERNAME=mongoadmin \
                -e MONGO_INITDB_ROOT_PASSWORD=1111 \
                -d mongo:3.6.22-xenial

fi
