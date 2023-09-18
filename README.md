# platform-engineer-problem-3


## How to test locally

### Register a new user
Curl example
```shell
curl -X 'POST' \
  'http://localhost:8080/api/register' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "q1w2e3"
}'
```

Expected output
```json
{"id":2,"name":"John Doe","email":"john.doe@example.com"}
```
### Auth with user and password
```shell
curl -X 'POST' \
  'http://localhost:8080/api/auth' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "email": "john.doe@example.com",
  "password": "q1w2e3"
}'
```

Expected output
```json
{"message":"Successfully authenticated user.","id":"2","name":"John Doe","email":"john.doe@example.com","accessToken":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huLmRvZUBleGFtcGxlLmNvbSIsImlhdCI6MTY4MzY1Nzg1MX0.CWz_oMSXkgMuc79kcEFBE0FcyLwYzq23o-vbfJPyyx0"}
```

### Execute an API call
We tested here with GET on `/api/users'
```shell
curl -X 'GET' \
  'http://localhost:8080/api/users' \
  -H 'accept: */*' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huLmRvZUBleGFtcGxlLmNvbSIsImlhdCI6MTY4MzY1Nzg1MX0.CWz_oMSXkgMuc79kcEFBE0FcyLwYzq23o-vbfJPyyx0'
```

Expected output
```json
[{"id":1,"name":"John Doe","email":"john.doe@example.com"}]%
```

pipeline:
 - Instalação de dependencias da aplicação gerando o WAR
 - Criar ecr caso não exista dando permissão para o service lambda
    - if $(aws ecr describe-images --repository-name my-ecr-repo) ; then echo ECR Exist; else aws ecr create-repository --repository-name my-ecr-repo; fi
 - Efetuar o docker build
 - Efetuar o upload da imagem para o ecr
 - Deploy da imagem na lambda

 terraform
  - Criar lambda container
  - Data para buscar a URL da imagem
  - Criar role com permissão de acesso ao ecr

Qualidade:
 - Colocar testes unitários
 - Test e2e
 - Colocar sonarQube como SAST