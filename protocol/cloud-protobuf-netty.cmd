set protobuf=D:\protoc-3.9.1-win64\bin\protoc.exe
%protobuf% ./addressbook.proto --java_out ../src/main/java
rem %protobuf%  ./addressbook.proto --go_out=plugins=grpc:../src/main/test