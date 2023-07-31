namespace App.Usecases

open System
open App.Domain
open App.Boundaries

module FetchCompany =
    let exec (id: int32) : Async<CompanyProfile> =
        async {
            do! Async.Sleep 1000
            return! CompanyRepository.findCompanyById id
        }
