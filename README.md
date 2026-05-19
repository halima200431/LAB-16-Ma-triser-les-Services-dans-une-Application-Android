# LAB 16 - Maîtriser les Services dans une Application Android

## 1. Présentation du lab

Ce projet Android a pour objectif de comprendre et d’implémenter les **Services Android** à travers une application de chronomètre développée entièrement en **Java**.

L’application permet de lancer un chronomètre en arrière-plan à l’aide d’un **Foreground Service**, tout en gardant une communication directe avec l’interface grâce à un **Bound Service**.

Le service continue à fonctionner même lorsque l’utilisateur quitte l’application, grâce à une notification persistante affichant le temps écoulé.

---

## 2. Objectifs du lab

Les objectifs principaux de ce lab sont :

- Créer une application Android en Java utilisant un Service.
- Comprendre le rôle d’un **Foreground Service**.
- Afficher une notification persistante pendant l’exécution du service.
- Utiliser un **Bound Service** pour permettre à l’Activity de communiquer avec le Service.
- Gérer correctement le démarrage et l’arrêt du service.
- Comprendre le cycle de vie d’un Service Android.
- Appliquer les bonnes pratiques liées aux services en arrière-plan.

---

## 3. Technologies utilisées

| Élément | Description |
|---|---|
| Langage | Java |
| IDE | Android Studio |
| SDK minimum | API 24 |
| Composant principal | Android Service |
| Type de service | Foreground Service + Bound Service |
| Interface | XML |
| Notification | NotificationCompat |
| Thread de fond | ScheduledExecutorService |

---

## 4. Fonctionnalités de l’application

L’application permet de :

- Démarrer un chronomètre depuis l’interface.
- Lancer un service en premier plan.
- Afficher une notification persistante indiquant le temps écoulé.
- Continuer le chronomètre même si l’application est fermée.
- Reconnecter l’Activity au Service grâce au mécanisme de binding.
- Arrêter proprement le service depuis l’interface.
- Supprimer la notification lors de l’arrêt du service.

---

## 5. Structure du projet

```text
ServiceChronometreJava/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/servicechronometrejava/
│   │   │   │   ├── MainActivity.java
│   │   │   │   └── ChronometreService.java
│   │   │   │
│   │   │   ├── res/layout/
│   │   │   │   └── activity_main.xml
│   │   │   │
│   │   │   └── AndroidManifest.xml
│   │
│   └── build.gradle
│
├── build.gradle
├── settings.gradle
└── README.md
```

---

## 6. Description des fichiers principaux

### 6.1 MainActivity.java

`MainActivity.java` représente l’interface principale de l’application.

Elle contient :

- Un bouton pour démarrer le service.
- Un bouton pour arrêter le service.
- Un TextView pour afficher le temps écoulé.
- Une connexion au service à travers `ServiceConnection`.
- L’utilisation de `bindService()` pour communiquer avec le service.
- L’utilisation de `startForegroundService()` pour démarrer le service sur Android 8.0 et plus.

### 6.2 ChronometreService.java

`ChronometreService.java` contient la logique du chronomètre.

Il contient :

- La classe interne `LocalBinder` pour permettre la communication avec l’Activity.
- La méthode `onCreate()` pour initialiser le service.
- La méthode `onStartCommand()` pour démarrer ou arrêter le service.
- La méthode `startForeground()` pour afficher une notification persistante.
- Un `ScheduledExecutorService` pour incrémenter le temps toutes les secondes.
- La méthode `updateNotification()` pour actualiser la notification.
- La méthode `onDestroy()` pour arrêter le thread et supprimer la notification.

### 6.3 activity_main.xml

Ce fichier définit l’interface graphique de l’application.

Il contient :

- Un titre.
- Un affichage du temps au format `mm:ss`.
- Un bouton de démarrage.
- Un bouton d’arrêt.

### 6.4 AndroidManifest.xml

Le fichier Manifest déclare :

- Les permissions nécessaires pour les notifications.
- Les permissions nécessaires aux Foreground Services.
- Le service `ChronometreService`.
- Le type du service : `dataSync`.

---

## 7. Permissions utilisées

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />
```

### Explication

- `POST_NOTIFICATIONS` : permet d’afficher une notification sur Android 13 et plus.
- `FOREGROUND_SERVICE` : permet d’utiliser un service en premier plan.
- `FOREGROUND_SERVICE_DATA_SYNC` : permet de déclarer un Foreground Service de type `dataSync`.

---

## 8. Principe de fonctionnement

Le fonctionnement général de l’application est le suivant :

1. L’utilisateur clique sur le bouton **DÉMARRER SERVICE**.
2. L’Activity crée un `Intent` vers `ChronometreService`.
3. Le service démarre avec `startForegroundService()`.
4. Le service appelle `startForeground()` pour afficher une notification persistante.
5. Le chronomètre s’incrémente chaque seconde grâce à `ScheduledExecutorService`.
6. La notification est mise à jour en direct.
7. L’Activity se connecte au service avec `bindService()`.
8. Le temps est affiché dans l’interface.
9. Lorsque l’utilisateur clique sur **ARRÊTER SERVICE**, le service est arrêté proprement.
10. Le thread est fermé et la notification disparaît.

---

## 9. Partie démo

### 9.1 Écran initial de l’application

Au lancement de l’application, le chronomètre affiche `00:00`.




https://github.com/user-attachments/assets/10728aa8-41af-441c-aeb6-d940f9ad1795



### 9.2 Démarrage du service

Après avoir cliqué sur le bouton **DÉMARRER SERVICE**, le chronomètre commence à tourner.

Résultat attendu :

- Le temps augmente chaque seconde.
- Une notification persistante apparaît.
- Le service passe en mode Foreground Service.



### 9.3 Notification persistante

La notification affiche le temps écoulé en direct.

---

### 9.4 Fermeture de l’application

Lorsque l’utilisateur quitte l’application, le service continue à fonctionner.

Résultat attendu :

- L’application peut être fermée.
- Le chronomètre continue.
- La notification reste visible.



---

### 9.5 Retour vers l’application

Lorsque l’utilisateur rouvre l’application, l’Activity se reconnecte au service grâce au Bound Service.

Résultat attendu :

- Le temps affiché correspond au temps réel du service.
- Le chronomètre ne revient pas à zéro.
- La communication Activity-Service fonctionne correctement.



### 9.6 Arrêt du service

Lorsque l’utilisateur clique sur **ARRÊTER SERVICE**, le service s’arrête.

Résultat attendu :

- Le chronomètre revient à `00:00`.
- La notification disparaît.
- Le thread de fond est arrêté.
- Le service est détruit proprement.



---

## 10. Résultats obtenus

À la fin du lab, l’application permet de vérifier que :

- Le Foreground Service fonctionne correctement.
- La notification persistante est bien affichée.
- Le chronomètre continue même lorsque l’application est fermée.
- Le Bound Service permet à l’Activity de récupérer les données du service.
- L’arrêt du service libère correctement les ressources.

---

## 11. Concepts Android compris

### Foreground Service

Un Foreground Service est un service visible pour l’utilisateur grâce à une notification persistante. Il est utilisé pour les tâches importantes qui doivent continuer même lorsque l’application n’est plus au premier plan.

### Bound Service

Un Bound Service permet à un composant, comme une Activity, de se connecter au service pour échanger des données ou appeler ses méthodes.

### Notification Channel

Depuis Android 8.0, les notifications doivent appartenir à un canal de notification. Dans ce projet, le canal utilisé est :

```java
chrono_channel
```

### START_STICKY

`START_STICKY` indique au système Android qu’il peut redémarrer le service si celui-ci est tué par manque de ressources.

### ScheduledExecutorService

`ScheduledExecutorService` est utilisé pour exécuter une tâche répétitive toutes les secondes, sans bloquer le thread principal de l’application.

---



## 12. Conclusion

Ce lab permet de comprendre le fonctionnement des Services Android à travers un exemple pratique de chronomètre.

L’application montre comment lancer une tâche durable avec un Foreground Service, comment maintenir une notification active, et comment permettre à l’interface de communiquer avec le service grâce à un Bound Service.

Ce travail constitue une base importante pour développer des applications Android capables d’exécuter des traitements en arrière-plan de manière propre, contrôlée et conforme aux restrictions modernes d’Android.

---

## 13. Auteur

Projet réalisé dans le cadre du cours de Programmation Mobile Android avec Java.

**Étudiante : Halima**

