import java
import semmle.code.java.dataflow.TaintTracking
import semmle.code.java.security.ExternalProcess


class PathVariableSource extends DataFlow::Node {
  PathVariableSource() {
    exists(Annotation a, Parameter p |
      p.getAnAnnotation() = a and
      a.getType().hasName("PathVariable") and
      this.asParameter() = p
    )
  }
}

class CommandInjectionConfig extends TaintTracking::Configuration {
  CommandInjectionConfig() {
    this = "commandInjectionConfig"
  }
  
  override predicate isSource(DataFlow::Node source) {
    source instanceof PathVariableSource
  }
  
  override predicate isSink(DataFlow::Node sink) {
    sink.asExpr() instanceof ArgumentToExec
  }
}

from PathVariableSource src, DataFlow::Node sink, CommandInjectionConfig cfg
where cfg.hasFlow(src, sink)
select src, sink, src.getLocation(), sink.getLocation()
