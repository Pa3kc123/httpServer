package sk.pa3kc.miniprojects.data

fun interface HttpAction {
    operator fun invoke(req: HttpRequest, res: HttpResponse)
}
