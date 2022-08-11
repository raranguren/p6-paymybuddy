Generated with Plant Text UML generator

- Online generator: https://www.planttext.com/
- Help: https://plantuml.com/class-diagram

```
@startuml

interface UserDetails {
  +getPassword()
  +getUserName()
}
interface UserDetailService {
  +loadUserByUsername()
}
interface WebMvcConfigurer {
  +addViewControllers()
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
  class WalletController< 1 > {
    +getTransferPage()
    +postPay()
  }
}

package service {
  class UserService< 1 > implements UserDetailService {
    +getAuthenticatedUser()
  }
  class WalletService< 1 > {
    +getWalletForUser()
    +getWalletForAuthenticatedUser()
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
    +amountInCents : int
    +time : Timestamp
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
    +findByUser()
  }
}

WalletController *--> WalletService

WalletService *-> TransferService
UserService <-* WalletService
UserPrincipal <. UserService : creates
UserPrincipal " " *--> "1  " User
User "1  " <--o "1  " Wallet
Wallet "2  " <--* " 0..*  " Transfer : sender /    \n receiver
Wallet o-> " 0..*  " Wallet : contacts
UserService *--> UserRepository
WalletService *--> WalletRepository
TransferService *--> TransferRepository
repository ..> User
repository ..> Wallet

repository ..> Transfer : creates entities

UserDetails -[hidden]>UserDetailService
UserRepository --|> JpaRepository
TransferRepository --|> JpaRepository
WalletRepository --|> JpaRepository


SpringSecurityConfig .[hidden]> WebMvcConfiguration
JpaRepository .[hidden]> space
space .[hidden]> WebMvcConfigurer
hide space

@enduml

