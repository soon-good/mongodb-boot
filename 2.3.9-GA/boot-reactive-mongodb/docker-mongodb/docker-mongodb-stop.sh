# mongodb docker container 중지 및 볼륨 삭제 스크립트

name_codingtest_mongodb='codingtest-mongodb'

cnt_codingtest_mongodb=`docker container ls --filter name=codingtest-mongodb | wc -l`
cnt_codingtest_mongodb=$(($cnt_codingtest_mongodb -1))

if [ $cnt_codingtest_mongodb -eq 0 ]
then
    echo "'$name_codingtest_mongodb' 컨테이너가 없습니다. 삭제를 진행하지 않습니다."

else
    echo "'$name_codingtest_mongodb' 컨테이너가 존재합니다. 기존 컨테이너를 중지하고 삭제합니다."
    docker container stop codingtest-mongodb
    rm -rf ~/env/docker/codingtest/volumes/codingtest-mongodb/*
    echo "\n'$name_codingtest_mongodb' 컨테이너 삭제를 완료했습니다.\n"
fi
