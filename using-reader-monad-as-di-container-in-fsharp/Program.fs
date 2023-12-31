open System
open Microsoft.AspNetCore.Builder
open Microsoft.Extensions.Hosting
open Microsoft.Extensions.DependencyInjection

[<EntryPoint>]
let main args =
    let builder = WebApplication.CreateBuilder(args)
    builder.Services.AddHttpClient
    let app = builder.Build()

    app.MapGet("/", Func<string>(fun () -> "Hello World!"))
    |> ignore

    app.Run()

    0 // Exit code
