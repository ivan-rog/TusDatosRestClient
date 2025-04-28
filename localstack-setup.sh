#!/bin/sh
echo "Initializing localstack secret manager"

touch secrets.json
cat > secrets.json << EOF
{
  "SM_CLIENT_USER":"$USER_CLIENT",
  "SM_CLIENT_PASSWORD":"$USER_PASSWORD"
}
EOF

awslocal secretsmanager create-secret \
    --name /secrets/tus-datos \
    --description "LocalStack Secret" \
    --secret-string file://secrets.json