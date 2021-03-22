# mongodb docker container repl 접속 스크립트

name_codingtest_mongodb='codingtest-mongodb'

cnt_codingtest_mongodb=`docker container ls --filter name=codingtest-mongodb | wc -l`
cnt_codingtest_mongodb=$(($cnt_codingtest_mongodb -1))

if [ $cnt_codingtest_mongodb -eq 0 ]
then
    echo "'$name_codingtest_mongodb' 컨테이너가 없습니다. 컨테이너를 구동해주세요."

else
    echo "'$name_codingtest_mongodb' 컨테이너의 BASH 쉘 접속을 시작합니다."
    docker container exec -it codingtest-mongodb sh
fi
