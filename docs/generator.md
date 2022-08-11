Classes diagram generated with Plant Text UML generator:
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
    +description : String
    +amountInCents : int
    +timeCompleted : Timestamp
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

WalletController o--> WalletService

WalletService o-> TransferService
UserService <-o WalletService
UserPrincipal <. UserService : creates
UserPrincipal " " o--> User
User <--o Wallet
Wallet "1..2  " <--o " *  " Transfer : sender /    \n receiver
Wallet o-> "   *    " Wallet : contacts
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

''' layout helpers
SpringSecurityConfig .[hidden]> WebMvcConfiguration
JpaRepository .[hidden]> space
space .[hidden]> WebMvcConfigurer
hide space

@enduml
