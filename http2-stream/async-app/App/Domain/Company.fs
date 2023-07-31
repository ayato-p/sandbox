namespace App.Domain

type CompanyName = CompanyName of string

type CompanyProfile = { id: int32; name: CompanyName }
