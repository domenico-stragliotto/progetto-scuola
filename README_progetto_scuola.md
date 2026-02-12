# School Management System

## Requisiti

- Java 17+
- Maven 3.8+
- MySQL 8.0+

## Avvio

### 1. Avvio Applicazione

```bash
# Compila
mvn clean install -DskipTests

# Avvia
mvn spring-boot:run
O
./mvnw
```

L'applicazione sarà disponibile su: `http://localhost:8080`

---

## Credenziali di Accesso

### Utenti Predefiniti

| Username | Password | Ruolo        |
| -------- | -------- | ------------ |
| `admin`  | `admin`  | ADMIN + USER |
| `user`   | `user`   | USER         |

---

## API Testing - Guida Completa

### Base URL

```
http://localhost:8080
```

### Importa in Postman

```
Import → Link → http://localhost:8080/v3/api-docs
```

---

## Scenario di Test Completo

### Step 1: Login (Ottieni Token JWT)

**Endpoint:** `POST /api/authenticate`

**Request:**

```bash
curl -X POST http://localhost:8080/api/authenticate \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin"
  }'
```

**Payload:**

```json
{
  "username": "admin",
  "password": "admin"
}
```

**Response (200 OK):**

```json
{
  "id_token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTcwNzgzMDQwMH0..."
}
```

Il token è usato nelle chiamate successive come

```
Authorization: Bearer <id_token>
```

---

### Step 2: Crea Nuovo Utente

**Endpoint:** `POST /api/register`

**Request:**

```bash
curl -X POST http://localhost:8080/api/register \
  -H "Content-Type: application/json" \
  -d '{
    "login": "docente.rossi",
    "email": "mario.rossi@scuola.it",
    "password": "password123",
    "firstName": "Mario",
    "lastName": "Rossi",
    "langKey": "it"
  }'
```

**Utente creato!** Può fare login con `docente.rossi` / `password123`

---

### Step 3: Visualizza Lista Classi

**Endpoint:** `GET /api/classes`

**️ Questa API è pubblica** - NON serve autenticazione!

**Request:**

```bash
curl http://localhost:8080/api/classes?page=25 # Paginazione aumentata necessaria per vedere subito tutto le classi
```

**Response (200 OK):**

```json
[
  {
    "id": 1,
    "numero": 1,
    "sezione": "A",
    "note": "Classe Prima Sezione A"
  },
  {
    "id": 2,
    "numero": 1,
    "sezione": "B",
    "note": "Classe Prima Sezione B"
  },
  {
    "id": 3,
    "numero": 1,
    "sezione": "C",
    "note": "Classe Prima Sezione C"
  }
  // ... altre 22 classi (totale 25)
]
```

**25 classi disponibili** (dalla 1A alla 5E)

---

### Step 4: Crea Nuovo Alunno

**Endpoint:** `POST /api/alunnos`

**Richiede autenticazione!**

**Request:**

```bash
curl -X POST http://localhost:8080/api/alunnos \
  -H "Authorization: Bearer <TUO_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Giulia",
    "cognome": "Bianchi",
    "dataNascita": "2015-03-15",
    "classe": {
      "id": 1
    }
  }'
```

### Step 5: Crea Compito in Classe per l'Alunno

**Endpoint:** `POST /api/compito-in-classes`

**Request:**

```bash
curl -X POST http://localhost:8080/api/compito-in-classes \
  -H "Authorization: Bearer <TUO_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "data": "2024-02-12",
    "materia": "MATEMATICA",
    "risultato": 8.5,
    "alunno": {
      "id": 1
    }
  }'
```

**Materie disponibili:**

- `STORIA`
- `ITALIANO`
- `INGLESE`
- `MATEMATICA`

**Risultato:** numero decimale da 1.0 a 10.0

---

## Documentazione API

### Postman Collection

Importa il file: `postman_collection.json`

Oppure importa direttamente da URL:

```
http://localhost:8080/v3/api-docs
```

---

## Note

Alcuni dettagli notati ma non implementati per questo mvp per mancanza di tempo

Nelle richieste viene usato il DTO completo per l'entità, questo permetterebbe di passare anche campi di entità
correlate non necessari e che comunque non verrebbero salvati, sarebbe opportuno effettuare un refactor delle classi
nelle request per usare entità specifiche e sufficienti invece del DTO completo.

Nelle risposte i payload avranno dei valori null su campi di entità collegate, questo è dovuto ai mapper delle
risposte che non mappano completamente i campi delle entità collegate. Le opzioni sono 2: modificare i mapper per tornare
tutti i campi desiderati o modificare la configurazione di ritorno di jackson per non tornare campi null.
E' stata modificata per ora solo la classe AlunnoMapper per aggiungere la sezione in ritorno come prova.
