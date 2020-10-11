package sk.pa3kc.miniprojects.thread

import sk.pa3kc.miniprojects.CSV_DIR_PATH
import java.nio.file.*

object DirectoryWatcherThread : Runnable {
    override fun run() {
        /*val nioPath = Paths.get(CSV_DIR_PATH)
        val watchService = FileSystems.getDefault().newWatchService()
        nioPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE)

        while (true) {
            val key: WatchKey

            try {
                key = watchService.take()
            } catch (e: Exception) {
                e.printStackTrace()
                break
            }

            for (event in key.pollEvents()) {
                val kind = event.kind()

                if (kind == StandardWatchEventKinds.OVERFLOW) continue

                val ev = event as WatchEvent<Path>
                val name = ev.context()
                val child = nioPath.resolve(name)

                println("${kind.name()}: $child")
            }

            if (!key.reset()) break
        }*/
    }
}
