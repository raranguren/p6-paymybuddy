Generated with Plant Text UML generator

- Online generator: https://www.planttext.com/
- Help: https://plantuml.com/class-diagram

```uml

@startuml


interface UserDetails {
+getPassword()
+getUserName()
}
interface UserDetailService {
+loadUserByUsername()
}
interface WebMvcConfigurer {
+public addViewControllers()
}
interface JpaRepository {
+{method} CRUD methods
}


package configuration {
class SpringSecurityConfig< 1 > {
+securityFilterChain()
+getPasswordEncoder()
}
class WebMvcConfiguration< 1 > implements WebMvcConfigurer{
}
}

package controller {
class TransferViewController< 1 > {
+getPage()
+postPay()
}
class AddContactViewController< 1 > {
+getPage()
+postAdd()
}
}

package service {
class UserService< 1 > implements UserDetailService {
}
class WalletService< 1 > {
+getContacts()
+getTransfers()
+addContact()
+doTransfer()
}
class TransferService< 1 > {
+getTransfersForWallet()
+addTransfer()
}
}

package model {
class UserPrincipal implements UserDetails {
}

entity User #e4f7e6 {
+id : Long
+email : String
+password : String
}

entity Wallet #e4f7e6 {
+id : Long
+profileName : String
+balanceInCents : int
}

entity Transfer #e4f7e6 {
+id : Long
+amount : int
}
}

package repository {
interface UserRepository {
+findByEmail()
}
interface TransferRepository {
+findBySender()
+findByReceiver()
}
interface WalletRepository {
}
}

TransferViewController *--> WalletService
AddContactViewController *--> WalletService

TransferService *-> WalletService
UserPrincipal <. UserService : creates
UserPrincipal " " *--> "1  " User
User "1   " *--> "1 "Wallet
Wallet "2  " <--* " 0..*  " Transfer : sender /    \n receiver
Wallet o-> Wallet : contacts\n0..*   
UserService *--> UserRepository
WalletService *--> WalletRepository
TransferService *--> TransferRepository
repository ..> User
repository ..> Wallet
repository ..> Transfer : creates entities

UserRepository --|> JpaRepository
TransferRepository --|> JpaRepository
WalletRepository --|> JpaRepository

UserService .[hidden]> TransferService
UserDetails .[hidden]> UserDetailService
SpringSecurityConfig .[hidden]> WebMvcConfiguration
JpaRepository .[hidden]> space
space .[hidden]> WebMvcConfigurer
hide space

@enduml

```