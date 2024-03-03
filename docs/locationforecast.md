

# Location Forecast API

eksempel-kall: `https://api.met.no/weatherapi/locationforecast/2.0/edr/collections/complete/position?coords=POINT(10+60)&z=123`

Dette kallet gir tilbake en ~4500 linjer lang json fil med generell værmelding fra gitte kordinater 

## Hvordan kan vi customize API-kallet?

`https://api.met.no/weatherapi/locationforecast/2.0/edr/collections/complete/position?coords=POINT(LONGTITUDE+LANGTIUDE)&z=HEIGHT`

### Kordinatsystem

Over har vi fjernet "tallene" fra våre gitte variabler, og disse styrer hvor vi spør API-en om værmeldingen. 


Vi ber om værmeldingen fra et tredimmensjonalt kordinatsystem: 
- LONGTITUDE (Breddegrad)
- LATTITUDE (Lengdegrad / høydegrad)
- HEIGHT (meter over havet)

eller som i minecraft, X, Y og Z kordinater. bare med hele jordkloden i stedenfor. 

Vi kan lett fine kordinatene til hvor enn vi vil med tjenester som denne:  https://www.gps-coordinates.net/ 

IFI har disse kordinatene: 
- LONG: `10.71799373626709`
- LAT: `59.94347381591797`
- HEIGHT: `50` (tilfeldig tall)

Da kan vi danne dette API-kallet: `https://api.met.no/weatherapi/locationforecast/2.0/edr/collections/complete/position?coords=POINT(10.71799373626709+59.94347381591797)&z=50`

Vi kan også droppe høyden fra kallet: 
`https://api.met.no/weatherapi/locationforecast/2.0/edr/collections/complete/position?coords=POINT(10.71799373626709+59.94347381591797)`


### Collections

Vi kan også endre en til variabel i spørringen, som er: `/complete/`

Denne beskriver formatet på spørringen, og vi har 3 alternativer her:

- `/complete` (~4500 linjer)
    - JSON forecast with all values. This will shortly be expanded with probabilities, so that each variable will be repeated for a set of percentiles.
    eks: `https://api.met.no/weatherapi/locationforecast/2.0/edr/collections/complete/position?coords=POINT(10.71799373626709+59.94347381591797)`

- `/compact` (~3000 linjer)
    - A shorter version with only the most used parameters (if you feel something is missing, please let us know) 
    eks: `https://api.met.no/weatherapi/locationforecast/2.0/edr/collections/compact/position?coords=POINT(10.71799373626709+59.94347381591797)`

- `/classic`
    - The old XML format, now considered legacy. It matches the output of version 1.9 closely, but includes more time periods. Future new parameters will not be added to this version.
    eks: `https://api.met.no/weatherapi/locationforecast/2.0/edr/classic/complete/position?coords=POINT(10.71799373626709+59.94347381591797)`


Hentet fra https://api.met.no/weatherapi/locationforecast/2.0/documentation


## Respons 

⚠️ // TODO// ⚠️

Må forstå/skrive mer om hvordan responsen fra API-fungerer.
- Hvilken data jobber vi med? 
- Hvordan aksesserer vi den? 



## Tanker rundt EDR vs vanlige API-kall


La oss sammenligne et vanlig API-kall vs. EDR gitt disse kordinatene:

- LAT: `10`
- LONG: `60`

vanlig:
`https://api.met.no/weatherapi/locationforecast/2.0/complete?lat=60&lon=10` 

EDR: `https://api.met.no/weatherapi/locationforecast/2.0/edr/collections/complete/position?coords=POINT(10+60)`

Tyngre? Bedre? Mer modulær? Stay tuned 👀


## Kotlin eksempel: 

### Kalle på API:

```kotlin
fun main(){
    val client = HttpClient { install(ContentNegotiation){ gson() } } // KTOR client

    // IFI coordinates
    val LATITUDE = 60
    val LONGITUDE = 10
    val HEIGHT = 0

    val path = "https://api.met.no/weatherapi/locationforecast/2.0/edr/collections/complete/position?coords=POINT($LONGITUDE+$LATITUDE)&z=$HEIGHT"

    runBlocking {
        val result = client.get(path)
        println("Status: ${result.status}")
    }
}
```
(Se også link til kotlin fil)


### Lese respons:
    // todo 
