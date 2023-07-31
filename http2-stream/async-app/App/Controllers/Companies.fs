namespace App.Controllers

open System
open System.Threading
open System.Threading.Tasks
open Microsoft.AspNetCore.Http

open FSharp.Control

open App.Domain
open App.Usecases

module ResponseJson =
    type Company = { Id: int32; CompanyName: string }

    let ofDomain (company: App.Domain.CompanyProfile) : Company =
        { Id = company.id
          CompanyName = company.name |> (fun (CompanyName cn) -> cn) }

module AsyncCompanies =

    let controller (limit: int32) : ResponseJson.Company taskSeq =
        taskSeq {
            let! fullIds = FetchCompanyIds.exec () |> Async.StartAsTask

            for chunk in
                fullIds
                |> Seq.take limit
                |> Seq.chunkBySize 10
                |> Seq.map (Seq.map FetchCompany.exec) do
                let! companies = chunk |> Async.Parallel |> Async.StartAsTask

                for company in companies do
                    yield company |> ResponseJson.ofDomain
        }


module CompanyIds =
    let controller (token: CancellationToken) : Task<int32 seq> =
        FetchCompanyIds.exec ()
        |> fun a -> Async.StartAsTask(a, cancellationToken = token)

module Company =
    let controller (id: int32) (token: CancellationToken) : Task<ResponseJson.Company> =
        async {
            let! company = FetchCompany.exec id
            return ResponseJson.ofDomain company
        }
        |> fun a -> Async.StartAsTask(a, cancellationToken = token)
