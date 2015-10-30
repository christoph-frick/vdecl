package vdecl

class FileEvent {

    enum Type { CREATE, DELETE, CHANGE }

    Type type
    File file

    static FileEvent build(Type type, File file) {
        new FileEvent(type:type, file:file)
    }

}