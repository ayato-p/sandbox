namespace App.Boundaries

open System
open System.IO
open System.Reflection
open App.Domain

module CompanyRepository =
    let private companies =
        let assembly = Assembly.GetExecutingAssembly()
        use stream = assembly.GetManifestResourceStream("App.Properties.companies.txt")
        use reader = new StreamReader(stream)

        reader
            .ReadToEnd()
            .Split([| "\r\n"; "\r"; "\n" |], StringSplitOptions.None)

    let findCompanyById (id: int32) : Async<CompanyProfile> =
        async {
            return
                { id = id
                  name = id - 1 |> Array.get companies |> CompanyName }
        }
