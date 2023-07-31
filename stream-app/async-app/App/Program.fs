open System
open System.Threading
open System.Threading.Tasks
open System.Threading.RateLimiting
open Microsoft.AspNetCore.Builder
open Microsoft.AspNetCore.RateLimiting
open Microsoft.Extensions.Hosting

open System.Collections.Generic
open App.Controllers

[<EntryPoint>]
let main args =
    let builder = WebApplication.CreateBuilder(args)
    let app = builder.Build()

    app.MapGet("/", Func<string>(fun () -> "Hello World!"))
    |> ignore

    app.MapGet("/companyIds", Func<CancellationToken, Task<int32 seq>> CompanyIds.controller)
    |> ignore

    app.MapGet(
        "/companies/{id}",
        Func<int32, CancellationToken, Task<ResponseJson.Company>>(fun id token -> Company.controller id token)
    )
    |> ignore

    app.MapGet(
        "/companies",
        Func<int, IAsyncEnumerable<ResponseJson.Company>>(fun limit -> AsyncCompanies.controller limit)
    )
    |> ignore

    app.Run()

    0 // Exit code
