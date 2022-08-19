cd /nfs/production/hhe/pride/dev/original/fairdomhub
pwd
rm -f fairdomhub.xml
wget https://fairdomhub.org/data_files/4969/download --no-check-certificate
mv download fairdomhub.xml
cd /nfs/production/hhe/pride/prod/ddi/original/fairdomhub
pwd
rm -f fairdomhub.xml
wget https://fairdomhub.org/data_files/4969/download --no-check-certificate
mv download fairdomhub.xml

