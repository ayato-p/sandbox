namespace App.Controller

open System.Reflection
open System.Threading.Tasks
open Microsoft.AspNetCore.Http

type NoTitleArticle = { content: string }
type NormalArticle = { title: string; content: string }

type Article =
    | NoTitle of NoTitleArticle
    | NormalArticle of NormalArticle
    static member BindAsync (context: HttpContext) (paramInfo: ParameterInfo) : ValueTask<Article> =
        NormalArticle { title = "Good bye"; content = "???" }
        |> ValueTask.FromResult
