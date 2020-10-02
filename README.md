# pto-proxy
Tynn proxy som videresender kall på tvers av clustere


## Hvordan registrere i api-gw
https://stackoverflow.com/c/nav-it/questions/324

curl -u NAV_IDENT:PASSWORD -v -H "kilde: noFasit" -H "Content-Type: application/json" -XPUT https://api-management.nais.adeo.no/rest/v2/katalog/applikasjoner/<APP> -d @api-gw/<APP>/katalog_<APP>.json 

curl -u NAV_IDENT:PASSWORD -v -H "kilde: noFasit" -H "Content-Type: application/json" -XPUT https://api-management.nais.adeo.no/rest/v2/register/deploy/<APP> -d @api-gw/<APP>/register_(dev/prod)-<APP>.json 

