# pto-proxy
Tynn proxy som videresender kall på tvers av clustere


## Hvordan registrere i api-gw
https://stackoverflow.com/c/nav-it/questions/324

curl -u NAV_IDENT:PASSWORD -v -H "kilde: noFasit" -H "Content-Type: application/json" -XPUT https://api-management.nais.adeo.no/rest/v2/katalog/applikasjoner/<APP> -d @api-gw/<APP>/katalog_<APP>.json 

curl -u NAV_IDENT:PASSWORD -v -H "kilde: noFasit" -H "Content-Type: application/json" -XPUT https://api-management.nais.adeo.no/rest/v2/register/deploy/<APP> -d @api-gw/<APP>/register_(dev/prod)-<APP>.json 

## API-GW
For å kunne snakke med api-gw så trengs det en nøkkel pr applikasjon.
Disse ligger i secreten **pto-proxy-api-gw-keys**.

Hent nøkkler:
`kubectl describe secret pto-proxy-api-gw-keys`

Endre nøkkel:
`kubectl edit secret pto-proxy-api-gw-keys`

Lag secret:
`kubectl create secret generic pto-proxy-api-gw-keys --from-literal=API_GW_KEY_APP_1=<secret> --from-literal=API_GW_KEY_APP_2=<secret> `]

API-GW nøkklene blir lagt i vault og må legges inn som en k8s secret manuelt.

