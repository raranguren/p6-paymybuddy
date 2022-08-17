Classes diagram generated with Plant Text UML generator:
- Online generator: https://www.planttext.com/
- Help: https://plantuml.com/class-diagram

```@startuml

package configuration {
  class LocaleConfig< 1 >
  class SpringSecurityConfig< 1 >
  class WebMvcConfiguration< 1 >
}

package controller {
  class MockBankController< 1 >
  class WebController< 1 >
}

package model {
  class BillingDetails
  class Invoice
  entity Transfer
  entity User
  entity Wallet
  Transfer "*" o--> "2" Wallet : sender/receiver
  User o-up-> Wallet
  class UserPrincipal
  UserPrincipal o-left-> User
  Wallet o-> "*" Wallet : connections
  Wallet o-left-> BillingDetails
}

package repository {
  interface TransferRepository
  interface UserRepository
  interface WalletRepository
}

package service {
  class MockBillingService< 1 > implements BillingService
  class UserService< 1 >
  class WalletService< 1 >
  class TransferService< 1 >
  TransferService *--> TransferRepository
  WalletService *-> UserService
  WalletService *--> TransferService
  WalletService *-> BillingService
}

WebController *--> WalletService
MockBankController *--> MockBillingService

WalletService *--> WalletRepository
UserService *--> UserRepository

BillingService .left.> Invoice
UserRepository .left.> User
UserService .left.> UserPrincipal

''' layout helpers
MockBillingService -[hidden]>configuration

@enduml