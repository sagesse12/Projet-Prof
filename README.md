# Projet-Prof
# 🍽️ Système de Gestion de Restaurant

## 📌 Aperçu du projet

Ce projet est une application console développée en **Java** permettant de gérer un restaurant.  
Il offre une solution complète pour la gestion du menu, des commandes, des tables, des réservations, du personnel, des stocks et de la facturation.

Le système est basé sur les principes de la **programmation orientée objet (POO)** et intègre plusieurs **patrons de conception (Design Patterns)**.

---

## ⚙️ Fonctionnalités principales

### 🍴 Gestion du menu
- Ajouter un plat
- Supprimer un plat
- Modifier un plat
- Afficher le menu

### 🪑 Gestion des tables
- Ajouter une table
- Supprimer une table
- Consulter l’état des tables (libre / occupée)

### 🧾 Gestion des commandes
- Créer une commande
- Modifier une commande
- Annuler une commande
- Finaliser une commande

### 📅 Réservations
- Créer une réservation
- Modifier une réservation
- Annuler une réservation

### 👨‍🍳 Gestion du personnel
- Ajouter / supprimer un employé
- Affecter le personnel aux tables ou sections

### 💰 Facturation et paiements
- Calcul automatique des factures
- Gestion des paiements

### 📦 Gestion des stocks
- Suivi des ingrédients
- Alerte de stock faible

### 📊 Reporting
- Rapports de ventes
- Plats les plus commandés

### 💾 Persistance des données
- Sauvegarde de l’état du système
- Chargement des données enregistrées

---

## 🧠 Concepts de Programmation Orientée Objet (POO)

Ce projet applique les principes suivants :

- **Encapsulation** : protection des données avec getters/setters
- **Héritage** : hiérarchie pour les plats et le personnel
- **Polymorphisme** : comportements différents selon les classes
- **Agrégation** : un restaurant contient tables, menus, commandes
- **Cohésion** : chaque classe a une responsabilité claire

---

## 🧩 Patrons de conception utilisés

- **Singleton** : gestion globale du restaurant / configuration
- **Factory** : création des plats, commandes et rapports
- **Observer** : notifications (commande prête, stock faible)
- **State** : gestion des états des commandes
- **Strategy** : différentes méthodes de paiement ou calcul de prix
- **Facade** : interface simplifiée pour tout le système

---

## 🖥️ Interface utilisateur

L’application fonctionne en **console Java**.


---

## 🚀 Installation et exécution

### 1. Cloner le projet
```bash
git clone https://github.com/sagesse12/Projet-Prof
cd Projet-Prof
