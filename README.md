# pto-proxy
Tynn proxy som videresender kall på tvers av clustere

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

```shell script
kubectl create secret generic pto-proxy-api-gw-keys \
--from-literal=API_GW_KEY_VEILARBAKTIVITET=XXX \
--from-literal=API_GW_KEY_VEILARBOPPFOLGING=XXX \
--from-literal=API_GW_KEY_VEILARBDIALOG=XXX \
--from-literal=API_GW_KEY_VEILARBLEST=XXX \
--from-literal=API_GW_KEY_VEILARBPERSON=XXX \
--from-literal=API_GW_KEY_VEILARBVEDTAKINFO=XXX \
--from-literal=API_GW_KEY_VEILARBREGISTRERING=XXX \
--from-literal=API_GW_KEY_VEILARBJOBBSOKERKOMPETANSE=XXX \    
```

### Hvordan registrere i api-gw
https://stackoverflow.com/c/nav-it/questions/324

curl -u NAV_IDENT:PASSWORD -v -H "kilde: noFasit" -H "Content-Type: application/json" -XPUT https://api-management.nais.adeo.no/rest/v2/katalog/applikasjoner/<APP> -d @api-gw/<APP>/katalog_<APP>.json 

curl -u NAV_IDENT:PASSWORD -v -H "kilde: noFasit" -H "Content-Type: application/json" -XPUT https://api-management.nais.adeo.no/rest/v2/register/deploy/<APP> -d @api-gw/<APP>/register_(dev/prod)-<APP>.json 

TODO: Configen som ligger i api-gw/ er ikke riktig enda siden den ikke tar med configen som blir brukt av veilarbproxy

Sørg for å sjekke hva som ligger i configen med endepunktet under før man gjør endringer på api-gw configen.
https://api-management.nais.adeo.no/rest/v2/katalog/innhold/<APP>