source deploy/util.sh
getProjectVersionFromPom
package
echo "deploy $projectversion"
deploy/create_deploy_scripts.sh
