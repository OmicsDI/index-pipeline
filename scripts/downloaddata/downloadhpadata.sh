cd /nfs/production/hhe/pride/prod/ddi/prod/hpa
pwd
rm -f hpa-for-covid19-portal.xml
wget https://www.proteinatlas.org/download/hpa-for-covid19-portal.xml --no-check-certificate
cd /nfs/production/hhe/pride/dev/prod/hpa
pwd
rm -f hpa-for-covid19-portal.xml
wget https://www.proteinatlas.org/download/hpa-for-covid19-portal.xml --no-check-certificate 
