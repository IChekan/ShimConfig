package modifiers;

import org.apache.log4j.Logger;
import util.SSHUtils;
import util.ShimValues;

/**
 * Created by Ihar_Chekan on 6/14/2017.
 */
public class CopyFilesFromCluster {

  final static Logger logger = Logger.getLogger( CopyFilesFromCluster.class);

  public void copySiteXmlFilesFromCluster () {
    for (int i = 0; i < ShimValues.getFilesToRetrieve().length; i++) {
      SSHUtils.copyFileBySSH(ShimValues.getSshUser(), ShimValues.getSshHost(), ShimValues.getSshPassword(),
        ShimValues.getFilesToRetrieve()[i], ShimValues.getPathToShim());
    }
  }

  public void copyKrb5conf () {
    if ( ShimValues.isShimSecured() ) {





      SSHUtils.copyFileBySSH( ShimValues.getSshUser(), ShimValues.getSshHost(), ShimValues.getSshPassword(),
        "/etc/krb5.conf", ShimValues.getPathToShim() );
      logger.warn( "krb5.conf copied from cluster to shim location. Please move it to appropriate place." );
    }
  }

}
