echo $1

# Set space as the delimiter
echo  "Inicial script create-job parametros $1"
IFS=','
read -a strarr <<< $1



java -jar jenkins-cli.jar -s http://localhost:8080 -webSocket groovy = < init.groovy.d/asdf/installTools.groovy installTools

asdf current
