package vdecl

import groovy.transform.ToString

@ToString
class FileEvent {

    enum Type { CREATE, DELETE, CHANGE }

    Type type
    File file

    static FileEvent build(Type type, File file) {
        new FileEvent(type:type, file:file)
    }

}